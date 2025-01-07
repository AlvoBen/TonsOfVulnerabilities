/*******************************************************************************************************************************
Apex Class Name : HUMArticleFeedbackHelperTest
Version         : 1.3
Created On      : 05/15/2014
Function        : Test the trigger written on Chatter Feed Item when a new chatter feed is added to a Knowledge article
Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Shruthi Karanth                                      05/15/2014                Initial version
 * Pranali Choudhari                                    08/3/2014                 Added additional methods
 * Pranali Choudhari                                    08/04/2014                Added best practices
 * Rakesh Patni				350876						04/11/2019				  Commented References to Article Types before Migration
 * Rakesh Patni				351706    					04/15/2019				  Updated Article Type Reference with Knowledge__Kav post migration.
 ****************************************************************************************************************************/

@isTest 
private class HUMArticleFeedbackHelperTest{
    /*  
     * Method name : testAcceptedFeedback
     * Description : This method tests the creation of feedcomment on changing status(Complete Accepted) of article feedback logged in as a user who hass access to the record
     * Return Type : void
     * Parameter   : none
     */ 
    private static testmethod void testAcceptedFeedback()
    {
		Knowledge__Kav kaConceptArticle = HUMTestDataHelperTest.getKnowledgeArticle();
		insert kaConceptArticle;
		test.startTest();

		KnowledgeArticleVersion kavDraftArticle1 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Draft' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		FeedItem fiArticleFeedback = new FeedItem();
		fiArticleFeedback.Type = 'TextPost';
		fiArticleFeedback.Body = '#publish:Please publish this concept';
		fiArticleFeedback.ParentId = kavDraftArticle1.KnowledgeArticleId;
		insert fiArticleFeedback;
		FeedItem fiArticleFeedback2 = new FeedItem();
		fiArticleFeedback2.Type = 'TextPost';
		fiArticleFeedback2.Body = '#publish:Please publish this concept again';
		fiArticleFeedback2.ParentId = kavDraftArticle1.KnowledgeArticleId;
		insert fiArticleFeedback2;
		List<Id> lstFeedItemId = new List<Id>{ fiArticleFeedback.Id,fiArticleFeedback2.Id };
		List<Article_Feedback__c> lstArtFeedbck = new List<Article_Feedback__c>();
		User u = HUMTestDataHelperTest.createTestUser('User1', 'Knowledge Manager');
		system.assertEquals(u.Email, 'User1@test.com');
		insert u;
		System.runAs(u)
		{
			for (Article_Feedback__c oArtFeed : [select id, ChatterFeedID__c, Feedback_Status__c from Article_Feedback__c where ChatterFeedID__c IN : lstFeedItemId])
			{
				oArtFeed.OwnerId = u.Id;
				oArtFeed.Feedback_Status__c = 'Complete Accepted';
				lstArtFeedbck.add(oArtFeed);
			}
			update lstArtFeedbck;

		}
		test.stoptest();
    }
    
    /*  
     * Method name : testRejectedFeedback
     * Description : This method tests the creation of feedcomment on changing status(Complete Rejected) of article feedback logged in as a user who hass access to the record
     * Return Type : void
     * Parameter   : none
     */
    private static testmethod void testRejectedFeedback()
    {
		Knowledge__Kav kaConceptArticle = HUMTestDataHelperTest.getKnowledgeArticle();
		insert kaConceptArticle;
		test.starttest();
		KnowledgeArticleVersion kavDraftArticle1 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Draft' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		FeedItem fiArticleFeedback = new FeedItem();
		fiArticleFeedback.Type = 'TextPost';
		fiArticleFeedback.Body = '#publish:Please publish this concept';
		fiArticleFeedback.ParentId = kavDraftArticle1.KnowledgeArticleId;
		insert fiArticleFeedback;
		FeedItem fiArticleFeedback2 = new FeedItem();
		fiArticleFeedback2.Type = 'TextPost';
		fiArticleFeedback2.Body = '#publish:Please publish this concept again';
		fiArticleFeedback2.ParentId = kavDraftArticle1.KnowledgeArticleId;
		insert fiArticleFeedback2;
		List<Id> lstFeedItemId = new List<Id>{ fiArticleFeedback.Id,fiArticleFeedback2.Id };
		List<Article_Feedback__c> lstArtFeedbck = new List<Article_Feedback__c>();
		User u = HUMTestDataHelperTest.createTestUser('User1', 'Knowledge Manager');
		system.assertEquals(u.LastName, 'test User1');
		insert u;
		System.runAs(u)
		{
			for (Article_Feedback__c oArtFeed : [select id, ChatterFeedID__c, Feedback_Status__c from Article_Feedback__c where ChatterFeedID__c IN : lstFeedItemId])
			{
				oArtFeed.OwnerId = u.Id;
				oArtFeed.Feedback_Status__c = 'Complete Rejected';
				lstArtFeedbck.add(oArtFeed);
			}
			update lstArtFeedbck;
		}
		test.stoptest();
    }
    
    /*  
     * Method name : testNegativeFeedback
     * Description : This method tests the exceptions if wrong atttributes are set while inserting feedcomment
     * Return Type : void
     * Parameter   : none
     */
    private static testmethod void testNegativeFeedback()
    {
		Knowledge__Kav kaConceptArticle = HUMTestDataHelperTest.getKnowledgeArticle();
		insert kaConceptArticle;
		test.starttest();
		system.assert(kaConceptArticle != null);
		KnowledgeArticleVersion kavDraftArticle1 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Draft' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		FeedItem fiArticleFeedback = new FeedItem();
		fiArticleFeedback.Type = 'TextPost';
		fiArticleFeedback.Body = '#publish:Please publish this concept';
		fiArticleFeedback.ParentId = kavDraftArticle1.KnowledgeArticleId;
		insert fiArticleFeedback;
		FeedItem fiArticleFeedback2 = new FeedItem();
		fiArticleFeedback2.Type = 'TextPost';
		fiArticleFeedback2.Body = '#publish:Please publish this concept again';
		fiArticleFeedback2.ParentId = kavDraftArticle1.KnowledgeArticleId;
		insert fiArticleFeedback2;
		List<Id> lstFeedItemId = new List<Id>{ fiArticleFeedback.Id,fiArticleFeedback2.Id };
		List<Article_Feedback__c> lstArtFeedbck = new List<Article_Feedback__c>();
		User u = HUMTestDataHelperTest.createTestUser('User1', 'Knowledge Manager');
		insert u;
		System.runAs(u)
		{
			for (Article_Feedback__c oArtFeed : [select id, ChatterFeedID__c, Feedback_Status__c from Article_Feedback__c where ChatterFeedID__c IN : lstFeedItemId])
			{
				//Set wrong attribute to CHatterFeedID__c
				oArtFeed.ChatterFeedID__c = oArtFeed.Id;
				oArtFeed.OwnerId = u.Id;
				oArtFeed.Feedback_Status__c = 'Complete Rejected';
				lstArtFeedbck.add(oArtFeed);
			}
			update lstArtFeedbck;
		}
		test.stoptest();
    }   
}