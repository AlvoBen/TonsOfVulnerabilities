/*******************************************************************************************************************************
Apex Class Name : HUMArticleFeedbackHelper 
Version         : 1.3
Created On      : 05/16/2014
Function        : This class is used for maintaining methods for custom object Article_Feedback__c

Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Shruthi Karanth               						05/16/2014                Initial version
 * Pranali Choudhari              						08/14/2014                Added recordtype in If condition
 * Pranali Choudhari			  						08/22/2014				  Modified code according to best practices
 ****************************************************************************************************************************/

public with sharing class HUMArticleFeedbackHelper
{
/*  
 * Method name : afterInsertArticleFeedback
 * Description : This method will be called from Trigger on custom object Article_Feedback__c
 * Return Type : void
 * Parameter   : List,Map 
 */
	public static void afterInsertArticleFeedback(List<Article_Feedback__c> lstArticleFeedback,Map<id,Article_Feedback__c> mapArticleFeedback)
	{
		List<FeedComment> lstFeedComment = new List<FeedComment>();
		try
		{
			//Loop over the Aticle Feedback in the Trigger context
			for (Article_Feedback__c afd : lstArticleFeedback)
			{ 
				//Check if the status is updated or not
				if((afd.Feedback_Status__c != mapArticleFeedback.get(afd.id).Feedback_Status__c) && afd.Feedback_Status__c  == System.Label.HUMArtFeedStatusAccepted && afd.RecordTypeId == HUMUtilityHelper.getRecordTypeID(Article_Feedback__c.getSobjectType(), System.label.HUMFeedRecType))
				{
					FeedComment fcomment = new FeedComment();
					fcomment.FeedItemId = afd.ChatterFeedID__c; //Id of the FeedItem on which you want to comment
					fcomment.CommentBody = System.Label.HUMArtCmntUpdate;
					lstFeedComment.add(fcomment);

				}    

				if((afd.Feedback_Status__c != mapArticleFeedback.get(afd.id).Feedback_Status__c) && afd.Feedback_Status__c  == System.Label.HUMArtFeedStatusRejected && afd.RecordTypeId == HUMUtilityHelper.getRecordTypeID(Article_Feedback__c.getSobjectType(), System.label.HUMFeedRecType))
				{
					FeedComment fcomment = new FeedComment();
					fcomment.FeedItemId = afd.ChatterFeedID__c; //Id of the FeedItem on which you want to comment
					fcomment.CommentBody = System.Label.HUMArtCmntReject;
					lstFeedComment.add(fcomment);

				}
			}    
			List<Database.SaveResult> lstFeedComInsert = Database.insert(lstFeedComment);
			HUMExceptionHelper.processSaveResults(lstFeedComInsert ,lstFeedComment ,'HUMArticleFeedbackHelper','afterInsertArticleFeedback','FeedComment'); 
		}
		catch(Exception e)
		{    
			HUMExceptionHelper.logErrors(e, 'HUMArticleFeedbackHelper', 'afterInsertArticleFeedback');
		}       
	}
}