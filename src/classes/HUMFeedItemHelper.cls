/*******************************************************************************************************************************
Apex Class Name : HUMFeedItemHelper 
Version         : 1.7
Created On      : 05/16/2014
Function        : This class is used for maintaining methods related to Chatter Feed item
Test Class      : HUMFeedItemHelperTest

Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Shruthi Karanth                                      05/16/2014                Initial version
 * Sara Thutupalli                                      07/18/2014                Updated the code to account for feedback on Draft article (REQ - 1955)
 * Sara Thutupalli                                      07/24/2014                Updated the code to account for #feedback on Draft articles And #publish on published articles (REQ - 2295)
 * Pranali Choudhari          17773                     08/12/2014                Updated code for some corner case scenarios
 * Pranali Choudhari          17773                     08/13/2014                Revamped the entire code for single feed insertion And removed unwanted code And optimised queries
 * Pranali Choudhari          17773                     08/14/2014                Changed code according to best practices
 * Pranali Choudhari          17773                     08/25/2014                Added utility class methods And cleaned up code
 * Palak Papneja              18736                     10/09/2014                Updated the code to limit feedback size to 750 characters
 * Palak Papneja              18736                     10/20/2014                Changed the error message for feedback size which exceeds 750 characters
 * Rakesh Patni				  350876					04/11/2019				  Commented References to Article Types before Migration
 * Rakesh Patni				  351706					04/15/2019				  Updated Article Type Reference with Knowledge__Kav post migration.
 * Moshitha Gunasekaran									05/23/2021				  US1801017 Made changes for Digital messaging to add subject line for feeditems
 * Moshitha Gunasekaran                                 06/14/2021                Made changes as per latest ICD to add FeedItemId And Date functionalities				
 * Moshitha Gunasekaran           				        06/18/2021                User Story 2377268:  T1PRJ0002081 / PR00092569 - MF #1799660  - SF - **Coaching ** - Push Notification to Go365
 * Moshitha Gunasekaran           				        07/09/2021                User Story 2464051:  T1PRJ0002081 / PR00092569 **Coaching** SF - TECH � Digital Messaging �Updates
 * Moshitha Gunasekaran           				        09/07/2021                User Story 2662404:  MAde changes to add link in Chatter
 ****************************************************************************************************************************/

public with sharing class HUMFeedItemHelper
{
	public FeedItem oFeedItem = new FeedItem();

	// Variable containing Id of the article 
	public String sArticleId = '';

	public FeedComment oFeedComment = new FeedComment();

	public List<FeedComment> lstFeedComment = new List<FeedComment>();

	// Map containing Article type as key and Article type as value
	public Map<String, sObject> mapArticleType = new Map<String, sObject>();

	public List<Article_Feedback__c> lstArticleFeedBck = new List<Article_Feedback__c>();

	public KnowledgeArticleVersion oKnowledgeArtVer = new KnowledgeArticleVersion();

	// Variable defining status of the article
	String sPublishStatus = '';

	Article_Feedback__c oArtFeedback = new Article_Feedback__c();

	/*
	* Method name : afterInsertFeedItem
	* Description : This method will be called from Feed Item Trigger to create article feedback and insert error comment
	* Return Type : void
	* Parameter   : FeedItem sent by trigger
	*/
	public void afterInsertFeedItem(List<FeedItem> lstFeedItem)
	{
		Map<Id, sObject> mapConcept = new Map<Id, sObject>();
		Map<Id, KnowledgeArticleVersion> mapKaVersion = new Map<Id, KnowledgeArticleVersion>();
		try
		{
			oFeedItem = lstFeedItem[0];
			sArticleId = oFeedItem.parentId;
			//parentID starts with kA- indicates that the object is a Knowledge Article  
			if (sArticleId.StartsWith(System.Label.HUMArtUserKA) && oFeedItem.type == System.Label.HUMArtFeedType)
			{
				if (oFeedItem.Body.containsIgnoreCase(System.Label.HUMArtUserFeedback) || oFeedItem.Body.containsIgnoreCase(System.Label.HUMArtUserPublish))
				{
					Profile userProf = [Select Id, PermissionsEditKnowledge from Profile where Id = :System.Userinfo.getProfileId()];
					//Check whether user has manage articles permission or not 
					if (userProf.PermissionsEditKnowledge || (!userProf.PermissionsEditKnowledge && !oFeedItem.Body.containsIgnoreCase(System.Label.HUMArtUserPublish)))
					{
						//Set publishstatus since salesforce imposes query restriction on KnowledgeArticleVersion to include publishstatus while querying
						sPublishStatus = getPublishStatus(oFeedItem);

						validateArticleFeedback(oFeedItem, sPublishStatus, sArticleId);
					}
					else if (!userProf.PermissionsEditKnowledge && oFeedItem.Body.containsIgnoreCase(System.Label.HUMArtUserPublish))
					{
						postErrorComment(oFeedItem.Id);
					}
				}
				else if (oFeedItem.Body.contains('#'))
				{
					postErrorComment(oFeedItem.Id);
				}
			}
            else if(sArticleId.StartsWith('005'))
			{
				List<CollaborationGroupMember> collabGrpMembers = [SELECT Id, CollaborationGroupId, MemberId, NotificationFrequency, CollaborationGroup.Name FROM CollaborationGroupMember where CollaborationGroup.Name = :GLOBAL_CONSTANT_CH_HUM.CoachingAdministrationGroup and MemberId = :sArticleId limit 1];
				if(null != collabGrpMembers && !collabGrpMembers.isEmpty())
				{
					String communityId = Network.getNetworkId();

					String feedElementId = oFeedItem.Id;
					//To Validate if coach is having Edit My Post access
					ConnectApi.FeedEntityIsEditable isEditable = ConnectApi.ChatterFeeds.isFeedElementEditableByMe(communityId, feedElementId);
					
					//if coach can edit post
					if (isEditable.isEditableByMe == true){
						//To get the FeedElement that need to be updated
						ConnectApi.FeedElement feedvalue = ConnectApi.ChatterFeeds.getFeedElement(communityId, oFeedItem.Id);
						postChatterWithSubject(feedvalue, feedElementId);
					}
				}
			}
		}
		catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'HUMFeedItemHelper', 'afterInsertFeedItem');
		}
	}

	/*
	* Method name : getPublishStatus
	* Description : This method will return the Publish status of the article
	* Return Type : String
	* Parameter   : FeedItem sent by trigger
	*/
	public String getPublishStatus(FeedItem oFeedItem)
	{
		if (oFeedItem.Body.containsIgnoreCase(System.Label.HUMArtUserFeedback))
		{
			return(System.Label.HUMArtStatusOnline);
		}
		else if (oFeedItem.Body.containsIgnoreCase(System.Label.HUMArtUserPublish))
		{
			return(System.Label.HUMArtStatusDraft);
		}
		else
		{
			return null;
		}
	}

	/*
	* Method name : validateArticleFeedback
	* Description : This method will return the Publish status of the article
	* Return Type : void
	* Parameter   : FeedItem sent by trigger, String, String
	*/
	public void validateArticleFeedback(FeedItem oFeedItem, String sPublishStatus, String sArticleId)
	{
		//Build the query to get the properties of the parent Article by passing parentId<Article ID> from the Chatter Post.    
		string strQryArticleVersion = 'select knowledgeArticleId, articlenumber, title,PublishStatus';
		strQryArticleVersion += ' from knowledgeArticleVersion where PublishStatus = \'' + String.escapeSingleQuotes(sPublishStatus) + '\'';
		strQryArticleVersion += 'and KnowledgeArticleId = :sArticleId';

		// Case: If feedback is more than 750 characters long, add the chatter feed Id to lstErrorFeedItems
		if (oFeedItem.Body.length() > Integer.valueOf(System.Label.HUMArticleFeedback_Max_Length))
		{
			postErrorOnSize(oFeedItem.Id);
		}
		else
		{
			List<KnowledgeArticleVersion> lstKav = new List<KnowledgeArticleVersion>();
			lstKav = Database.query(strQryArticleVersion);
			if (!lstKav.isEmpty())
			{
				oKnowledgeArtVer = lstKav[0];
				insertArticleFeedback();
			}
			//Case: The Knowledge Article not found, Add the chatter Feed Id to the lstErrorFeedItems
			else
			{
				postErrorComment(oFeedItem.Id);
			}
		}
	}

	/*
	* Method name : postErrorComment
	* Description : This method will be used to insert feedcomment
	* Return Type : void
	* Parameter   : Id
	*/
	private void postErrorComment(Id feedItemId)
	{
		FeedComment oFeedComment = new FeedComment();
		oFeedComment.FeedItemId = feedItemId; //Id of the FeedItem on which you want to comment
		oFeedComment.CommentBody = System.Label.HUMArtFeedError;
		lstFeedComment.add(oFeedComment);
		List<Database.SaveResult> lstFeedComInsert = Database.insert(lstFeedComment);
		HUMExceptionHelper.processSaveResults(lstFeedComInsert, lstFeedComment, 'HUMFeedItemHelper', 'postErrorComment', 'FeedComment');
	}

	/*
	* Method name : postErrorOnSize
	* Description : This method will be used to insert feedcomment
	* Return Type : void
	* Parameter   : Id
	*/
	private void postErrorOnSize(Id feedItemId)
	{
		FeedComment oFeedComment = new FeedComment();
		oFeedComment.FeedItemId = feedItemId; //Id of the FeedItem on which you want to comment
		oFeedComment.CommentBody = System.Label.HUMArtSizeError;
		lstFeedComment.add(oFeedComment);
		List<Database.SaveResult> lstFeedComInsert = Database.insert(lstFeedComment);
		HUMExceptionHelper.processSaveResults(lstFeedComInsert, lstFeedComment, 'HUMFeedItemHelper', 'postErrorOnSize', 'FeedComment');
	}


	/*
	* Method name : insertArticleFeedback
	* Description : This method will be called from afterInsertFeedItem method and contains code to create article feedback
	* Return Type : void
	* Parameter   : none
	*/
	private void insertArticleFeedback()
	{
		//the below query is executed to get the Feedback_Group__c on Draft/Published articles to set to the Article Feedback object.
		String sFeedbackQuery = 'SELECT id,recordtype.name,Q_A_Feedback_Group__c,KnowledgeArticleId FROM Knowledge__kav WHERE PublishStatus = \'' + String.escapeSingleQuotes(sPublishStatus) +
			'\' AND Language = \'' + String.escapeSingleQuotes(System.Label.HUMArticleFeedbackLanguage_English) + '\' and KnowledgeArticleId = \'' + String.escapeSingleQuotes(sArticleId) + '\'';
		List<Knowledge__kav> lstObject = new List<Knowledge__kav>();
		lstObject = database.query(sFeedbackQuery);
		String sFeedbackGroup = '';
		String recTypeName = '';
		if (!lstObject.isEmpty() && lstObject.size()>0)
		{
			sFeedbackGroup = lstObject[0].Q_A_Feedback_Group__c;
			recTypeName = lstObject[0].recordtype.name + '__kav';
		}
		//create the Article Feedback object and assign properties to it from the parent Article.                         
		oArtFeedback = New Article_Feedback__c(
			ChatterFeedID__c = oFeedItem.Id,
			Article_Type__c = recTypeName,
			Article_Number__c = oKnowledgeArtVer.articleNumber,
			Article_Title__c = oKnowledgeArtVer.Title,
			Article_Status__c = oKnowledgeArtVer.PublishStatus,
			Article_URL__c = System.Url.getSalesforceBaseUrl().toExternalForm() + '/' + oKnowledgeArtVer.KnowledgeArticleId,
			User_Comments__c = oFeedItem.body,
			Feedback_Group__c = sFeedbackGroup,
			RecordTypeId = HUMUtilityHelper.getRecordTypeID(Article_Feedback__c.getSobjectType(), System.label.HUMFeedRecType)
		);

		//Create the feedback object into the salesforce DB.           
		if (oArtFeedback != null)
		{
			lstArticleFeedBck.add(oArtFeedback);
			List<Database.SaveResult> lstArtFeedInsert = Database.insert(lstArticleFeedBck);
			HUMExceptionHelper.processSaveResults(lstArtFeedInsert, lstArticleFeedBck, 'HUMFeedItemHelper', 'insertArticleFeedback', 'Article_Feedback__c');
		}
	}

	/*
	* Method name : postChatterWithSubject
	* Description : This method will be used to insert feedcomment
	* Return Type : void
	* Parameter   : ConnectApi.FeedElement feedvalue
	*/
	@TestVisible 
	private void postChatterWithSubject(ConnectApi.FeedElement feedvalue, String feedElementId)
	{
		ConnectApi.FeedItemInput feedItemInput = new ConnectApi.FeedItemInput();
		ConnectApi.MessageBodyInput messageBodyInput = new ConnectApi.MessageBodyInput();
		ConnectApi.TextSegmentInput textSegmentInput = new ConnectApi.TextSegmentInput();
		boolean bSwitch_2377268 =  HUMUtilityHelper.isCRMFunctionalityON('2377268');

		messageBodyInput.messageSegments = new List<ConnectApi.MessageSegmentInput>();
		String sPersonAccountId;
		textSegmentInput.text = GLOBAL_CONSTANT_CH_HUM.CoachingSubject;
		messageBodyInput.messageSegments.add(textSegmentInput);
		//Get the feed element based on the type of message segment added
		List<ConnectApi.MessageSegment> segments = feedvalue.body.messageSegments;
		integer leng = segments.size();
		for(integer i=0; i < leng;i++)
		{
			if(segments.get(i) instanceof ConnectApi.EntityLinkSegment)
			{
				ConnectApi.EntityLinkSegment entiyval = (ConnectApi.EntityLinkSegment) segments.get(i);
				if(null != entiyval.reference)
				{
					sPersonAccountId = entiyval.reference.Id;
				}
			}
			if(segments.get(i) instanceof ConnectApi.HashtagSegment)
			{
				ConnectApi.HashtagSegment tagval = (ConnectApi.HashtagSegment) segments.get(i);
				ConnectApi.HashtagSegmentInput hashtagSegmentInputContact = new ConnectApi.HashtagSegmentInput();
				hashtagSegmentInputContact.tag = tagval.tag;   
				messageBodyInput.messageSegments.add(hashtagSegmentInputContact);
			}
			if(segments.get(i) instanceof ConnectApi.MarkupBeginSegment)
			{
				ConnectApi.MarkupBeginSegment markUpBeginval = (ConnectApi.MarkupBeginSegment) segments.get(i);
				ConnectApi.MarkupBeginSegmentInput markUpBeginSegmentInputContact = new ConnectApi.MarkupBeginSegmentInput();
				markUpBeginSegmentInputContact.altText = markUpBeginval.altText;
				markUpBeginSegmentInputContact.markupType = markUpBeginval.markupType;   
				markUpBeginSegmentInputContact.url = markUpBeginval.url;
				messageBodyInput.messageSegments.add(markUpBeginSegmentInputContact);
			}
			if(segments.get(i) instanceof ConnectApi.MarkupEndSegment)
			{
				ConnectApi.MarkupEndSegment markUpEndval = (ConnectApi.MarkupEndSegment) segments.get(i);
				ConnectApi.MarkupEndSegmentInput markUpEndSegmentInputContact = new ConnectApi.MarkupEndSegmentInput();
				markUpEndSegmentInputContact.markupType = markUpEndval.markupType;   
				messageBodyInput.messageSegments.add(markUpEndSegmentInputContact);
			}
			if(segments.get(i) instanceof ConnectApi.MentionSegment)
			{
				ConnectApi.MentionSegment mentionSeg = (ConnectApi.MentionSegment) segments.get(i);
				ConnectApi.MentionSegmentInput mentionSegmentInput = new ConnectApi.MentionSegmentInput();
				mentionSegmentInput.id = mentionSeg.record.id;   
				messageBodyInput.messageSegments.add(mentionSegmentInput);
			}
			if(segments.get(i) instanceof ConnectApi.TextSegment)
			{
				ConnectApi.TextSegment textSeg = (ConnectApi.TextSegment) segments.get(i);
				textSegmentInput = new ConnectApi.TextSegmentInput();
				textSegmentInput.text = textSeg.text;   
				messageBodyInput.messageSegments.add(textSegmentInput);
			}
			if(segments.get(i) instanceof ConnectApi.LinkSegment)
			{
				ConnectApi.LinkSegment linkSeg = (ConnectApi.LinkSegment) segments.get(i);
				ConnectApi.LinkSegmentInput linkSegmentInput = new ConnectApi.LinkSegmentInput();
				linkSegmentInput.url = linkSeg.url;   
				messageBodyInput.messageSegments.add(linkSegmentInput);
			}
		}
		if(feedvalue.capabilities != null && feedvalue.capabilities.link != null)
		{
			ConnectApi.LinkCapability feedLink = feedvalue.capabilities.link;
			ConnectApi.LinkCapabilityInput linkInput = new ConnectApi.LinkCapabilityInput();
			linkInput.url = feedLink.Url;
			linkInput.urlName = feedLink.urlName;
			ConnectApi.FeedElementCapabilitiesInput feedElementCapabilitiesInput = new ConnectApi.FeedElementCapabilitiesInput();
			feedElementCapabilitiesInput.link = linkInput;
			feedItemInput.capabilities = feedElementCapabilitiesInput;
		}
		
		//Update with title added to the Post
		feedItemInput.body = messageBodyInput;
		if (!Test.isRunningTest())
		{
			//If AccountId is referred by Coach for POST then add a duplicate Post with ParentId as AccountId and create a new POST
			//So that POST can be displayed in the Account Page chatter section
			if(String.isNotBlank(sPersonAccountId))
			{
				feedItemInput.subjectId = sPersonAccountId;
				feedItemInput.feedElementType = ConnectApi.FeedElementType.FeedItem;
				ConnectApi.FeedElement feedElementResponse = ConnectApi.ChatterFeeds.postFeedElement(Network.getNetworkId(), feedItemInput);
				//Delete the duplicate POST in future method as it doesnt allow deletion in single transaction
				if(feedElementResponse != null && string.isNotBlank(feedElementResponse.Id))
				{
					DeleteFeedItemPost(Network.getNetworkId(), feedElementId);
				}
			}
			else
			{
				ConnectApi.FeedElement editedFeedElement = ConnectApi.ChatterFeeds.updateFeedElement(Network.getNetworkId(), feedElementId, feedItemInput);
			}
		}
		if(bSwitch_2377268 && String.isNotBlank(sPersonAccountId))
		{
			PushNotification_S_HUM.callPushNotificationService(sPersonAccountId, feedElementId, 'FeedItem');
		}
	}

	/*
	* Method name : DeleteFeedItemPost
	* Description : This method will be used to delete feedcomment
	* Return Type : void
	* Parameter   : string communityId, string feedItemId
	*/
	@future(callout = true)
    public static void DeleteFeedItemPost(string communityId, string feedItemId)
    {
		try 
		{
			ConnectApi.ChatterFeeds.deleteFeedElement(communityId, feedItemId);
		}
		catch(exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'HUMFeedItemHelper', 'DeleteFeedItemPost');
		}
	}
}