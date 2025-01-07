/*******************************************************************************************************************************
Apex Class Name : HUMFeedItemTriggerTest 
Version         : 1.0
Created On      : 10/23/2014
Function        : This class is used for testing HUMFeedItemTrigger

Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Shruthi Karanth            18737                    10/23/2014                    Original version
 * Rakesh Patni			     350876						04/11/2019				  Commented References to Article Types before Migration
 * Rakesh Patni				 351706					04/15/2019				  Updated Article Type Reference with Knowledge__Kav post migration.
 ****************************************************************************************************************************/

@isTest
private class HUMFeedItemTriggerTest{
    /*  
     * Method name : testforArticleFeedbackTrigger
     * Description : This method tests the feed comment on article
     * Return Type : void
     * Parameter   : none
     */   
    private static testmethod void testforArticleFeedbackTrigger()
    {
		//Create a knowledge article to which the chatter feed item will be added
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
     * Method name : testforArticleFeedbackTriggerNegative
     * Description : This method tests the error scenario for bulk feeds
     * Return Type : void
     * Parameter   : none
     */   
    private static testmethod void testforArticleFeedbackTriggerNegative()
    {
		Knowledge__Kav kaArticle = HUMTestDataHelperTest.getKnowledgeArticle();
		insert kaArticle;
		test.starttest();
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
			insert lstFeedItem;
		}
		catch (Exception e)
		{
			System.assert(e.getMessage().contains(System.Label.HUMArtBulkError));
		}
		test.stoptest();
    }
}