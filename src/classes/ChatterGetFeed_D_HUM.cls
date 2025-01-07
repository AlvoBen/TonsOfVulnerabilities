/******************************************************************************************************************
Apex class Name    : ChatterGetFeed_D_HUM
Version            : 1.0
Function           : This class is for Data Access Layer for for chatter feed related activities
Created On         : May 12 2021
Test Class         : ChatterService_T_HUM

Modification Log:
*   Version        Developer                   Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Moshitha Gunasekaran        05/12/2021          Original Version
*   1.1			   Moshitha Gunasekaran        06/14/2021          Made changes as per latest ICD to add FeedItemId and Date functionalities				
*   1.2			   Moshitha Gunasekaran        07/09/2021          Made changes as per latest ICD to add PageNo functionalities				
*   1.3			   Moshitha Gunasekaran        11/09/2021          Made changes to add SentBy for the Feed 				
------------------------------------------------------------------------------------------------------------------- ------------------------------------------------------------------------------------------*/

public with sharing class ChatterGetFeed_D_HUM {
	
	private static Date dtFromDate = null;
	private static Date dtToDate = null;
	private static DateTime dtFromDateTimeGMT = null;
	private static DateTime dtToDateTimeGMT = null;
	private static list<ChatterGetFeedResponseDTO.ChatterFeedItemsDTO> unSortedList = new list<ChatterGetFeedResponseDTO.ChatterFeedItemsDTO>();
	private static list<ChatterGetFeedResponseDTO.ChatterFeedItemsDTO> sortedList = new list<ChatterGetFeedResponseDTO.ChatterFeedItemsDTO>();
	
	/*
	 * Method name : getFeeds
	 * Description : This method is to get chatter feeds based on inputs from Go365
	 * Return Type : ChatterGetFeedResponseDTO
	 * Parameter   : ChatterGetFeedRequestDTO
	 */
    public static ChatterGetFeedResponseDTO getFeeds(ChatterGetFeedRequestDTO chatterGetFeedRequest)
    {
    	list<Account> lstCoachingMembers = new list<Account>();
    	ChatterGetFeedResponseDTO getFeedResponse = null;
    	
    	try
    	{
			//Retrieved Coaching Member that sends message to tag from PersonId
	    	if(string.isNotBlank(chatterGetFeedRequest.PersonId))
	        {
	        	lstCoachingMembers = [SELECT Id, Enterprise_ID__c,ETL_Record_Deleted__c FROM Account WHERE Enterprise_ID__c = :chatterGetFeedRequest.PersonId and ETL_Record_Deleted__c = false limit 1];
	        }
	        else
	        {
	        	getFeedResponse = new ChatterGetFeedResponseDTO();
	        	getFeedResponse.Success = false;
	        	getFeedResponse.ErrorMessage = GLOBAL_CONSTANT_CH_HUM.PersonIdErrorMessage;
	        	return getFeedResponse;
	        }
	        if(null != lstCoachingMembers && !lstCoachingMembers.isEmpty())
	        {
				//If FeedItemId is blank then get all FeedItems based on dates.
				if(string.isBlank(chatterGetFeedRequest.FeedItemId))
				{
					ConnectApi.FeedElementPage feedvalue = null;
					ConnectApi.FeedElementPage getFeedValue = null;
					//If Date is available then parse and check if valid date. and If date not available then message retrieved for past 3 days
					if(string.isNotBlank(chatterGetFeedRequest.StartDate)) dtFromDate = EntitySearch_H_HUM.parsestringToDate(chatterGetFeedRequest.StartDate);
					if(string.isNotBlank(chatterGetFeedRequest.EndDate)) dtToDate = EntitySearch_H_HUM.parsestringToDate(chatterGetFeedRequest.EndDate);
					
					if((dtToDate == null || dtFromDate == null) && string.isBlank(chatterGetFeedRequest.PageNo))
					{
						dtFromDate = System.Today().addDays(-60);
						dtToDate = System.Today().addDays(1);
					}
					else if(dtToDate == null || dtFromDate == null)
					{
						if(string.isNotBlank(chatterGetFeedRequest.PageNo))
						{
							integer PageNum = integer.valueOf(chatterGetFeedRequest.PageNo);
							integer endAdddays = 60 * (PageNum - 1);
							integer startAdddays = 60 * (PageNum);
							if(PageNum == 1)
							{
								dtFromDate = System.Today().addDays(-60);
								dtToDate = System.Today().addDays(1);
							}
							else
							{
								dtFromDate = System.Today().addDays(-startAdddays);
								dtToDate = System.Today().addDays(-endAdddays);
							}
						}
					}
					else if(dtToDate != null || dtFromDate != null)
					{
						dtToDate = dtToDate.addDays(1);
					}
					DateTime tempFromDateLocal = DateTime.newInstance(dtFromDate.year(), dtFromDate.month(), dtFromDate.day(), 0, 0, 0); //constructs the start datetime in User's local Time zone. 
					DateTime tempToDateLocal = DateTime.newInstance(dtToDate.year(), dtToDate.month(), dtToDate.day(), 0, 0, 0);  //constructs the End datetime in User's local time zone.
					Date tempDateGMT = tempFromDateLocal.dateGMT();  //get the Date part in GMT
					Time tempTimeGMT = tempFromDateLocal.timeGMT();  //get the time part in GMT
					dtFromDateTimeGMT = DateTime.newInstanceGMT(tempDateGMT, tempTimeGMT);		//construct the start datetime in GMT(UTC) to use in SOQL WHERE Clause.
					tempDateGMT = tempToDateLocal.dateGMT();	//get the Date part in GMT
					tempTimeGMT = tempToDateLocal.timeGMT();	//get the time part in GMT
					dtToDateTimeGMT = DateTime.newInstanceGMT(tempDateGMT, tempTimeGMT);
					
					if (!Test.isRunningTest())
					{
						//If there are Account tagged in description then Search Feed will retrieve those values
						feedvalue = ConnectApi.ChatterFeeds.searchFeedElements(Network.getNetworkId(), lstCoachingMembers[0].Id, '', 100);
						//If there are Account tagged as parent then get will retrieve those values
						getFeedValue = ConnectApi.ChatterFeeds.getFeedElementsFromFeed(Network.getNetworkId(), ConnectApi.FeedType.Record, lstCoachingMembers[0].Id);
					}
					//Combine both feed items
					getFeedResponse = PopulateJoinFeedItems(feedvalue, getFeedValue, chatterGetFeedRequest);
				}
				else 
				{
					//If FeeedItem is passed in request then retrieve value only for that.
					ConnectApi.FeedElement feedvalue = ConnectApi.ChatterFeeds.getFeedElement(Network.getNetworkId(), chatterGetFeedRequest.FeedItemId);
					if(null != feedvalue)
					{
						getFeedResponse = PopulateFeedItem(feedvalue, chatterGetFeedRequest);
					}
				}
		        
	        }
	        else
	        {
	        	getFeedResponse = new ChatterGetFeedResponseDTO();
	        	getFeedResponse.Success = false;
	        	getFeedResponse.ErrorMessage = GLOBAL_CONSTANT_CH_HUM.PersonIdErrorMessage;
	        }
    	}
    	catch(Exception ex)
        {
        	List<Error_Log__c> errorList = new List<Error_Log__c>(); 
            Error_log__c error = HUMExceptionHelper.logOtherExceptions(ex, 'ChatterGetFeed_D_HUM','getFeeds');
            
            if(error!=null) 
            { 
                errorList.add(error);
                HUMExceptionHelper.saveLog(errorList, false);
            }
            getFeedResponse = new ChatterGetFeedResponseDTO();
            getFeedResponse.Success = false;
        	getFeedResponse.ErrorMessage = ex.getMessage(); 
        }
    	
    	return getFeedResponse;
	}
	
	/*
	* Method name : PopulateJoinFeedItems
	* Description : This method is to populate and combine both search and get feeditems. 
	* Parameter   : ConnectApi.FeedElementPage, ConnectApi.FeedElementPage, ChatterGetFeedRequestDTO
	* ReturnType  : ChatterGetFeedResponseDTO
	*/
	@TestVisible 
    private static ChatterGetFeedResponseDTO PopulateJoinFeedItems(ConnectApi.FeedElementPage feedvalue, ConnectApi.FeedElementPage getfeedvalue, ChatterGetFeedRequestDTO chatterGetFeedRequest )
    {
		ChatterGetFeedResponseDTO getFeedResponse = new ChatterGetFeedResponseDTO();
		map<string, ChatterGetFeedResponseDTO.ChatterFeedItemsDTO> mapFeedItems = new map<string, ChatterGetFeedResponseDTO.ChatterFeedItemsDTO>();
		//Loop through Search FeedItem list and add in map
		if(null != feedvalue && null != feedvalue.elements && !feedvalue.elements.isEmpty())
		{
			for(ConnectApi.FeedElement feelem: feedvalue.elements)
        	{
				if(!mapFeedItems.containsKey(feelem.Id))
				{
					if(feelem.createdDate >= dtFromDateTimeGMT && feelem.createdDate <= dtToDateTimeGMT)
					{
						ChatterGetFeedResponseDTO.ChatterFeedItemsDTO feeditm = new ChatterGetFeedResponseDTO.ChatterFeedItemsDTO();
						feeditm.FeedItemId = feelem.Id;
						if(null!=feelem.body && string.isNotBlank(feelem.body.text))
						{
							if(feelem.body.text.indexOf(GLOBAL_CONSTANT_CH_HUM.CoachingSubject) != -1)
							{
								feeditm.MessageText = feelem.body.text.substringAfter(GLOBAL_CONSTANT_CH_HUM.CoachingSubject);
								if(feeditm.MessageText.indexOf('CarePlanSubject:') != -1)
								{
									feeditm.MessageText = feeditm.MessageText.substringBefore('CarePlanSubject:') ;
								}
							}
							else
							{
								feeditm.MessageText = feelem.body.text;
								if(feeditm.MessageText.indexOf('CarePlanSubject:') != -1)
								{
									feeditm.MessageText = feeditm.MessageText.substringBefore('CarePlanSubject:') ;
								}
							}
						}
						if(null!=feelem.parent)
						{
							feeditm.FeedFrom = feelem.parent.name;
							if(feelem.parent.id.StartsWith('005'))
							{
								feeditm.FeedSentBy = 'Coach';
							}
							else
							{
								feeditm.FeedSentBy = 'Member';
							}
						}
						feeditm.MessageDateTime = feelem.createdDate;
					
						mapFeedItems.put(feelem.Id, feeditm);
					}
					
				}
			}
		}

		//Loop through Get FeedItem list and add in map
		if(null != getfeedvalue && null != getfeedvalue.elements && !getfeedvalue.elements.isEmpty())
		{
			for(ConnectApi.FeedElement feelem: getfeedvalue.elements)
        	{
				if(!mapFeedItems.containsKey(feelem.Id))
				{
					if(feelem.createdDate >= dtFromDateTimeGMT && feelem.createdDate <= dtToDateTimeGMT)
					{
						ChatterGetFeedResponseDTO.ChatterFeedItemsDTO feeditm = new ChatterGetFeedResponseDTO.ChatterFeedItemsDTO();
						feeditm.FeedItemId = feelem.Id;
						if(null!=feelem.body && string.isNotBlank(feelem.body.text))
						{
							if(feelem.body.text.indexOf(GLOBAL_CONSTANT_CH_HUM.CoachingSubject) != -1)
							{
								feeditm.MessageText = feelem.body.text.substringAfter(GLOBAL_CONSTANT_CH_HUM.CoachingSubject) ;
								if(feeditm.MessageText.indexOf('CarePlanSubject:') != -1)
								{
									feeditm.MessageText = feeditm.MessageText.substringBefore('CarePlanSubject:') ;
								}
							}
							else
							{
								feeditm.MessageText = feelem.body.text;
								if(feeditm.MessageText.indexOf('CarePlanSubject:') != -1)
								{
									feeditm.MessageText = feeditm.MessageText.substringBefore('CarePlanSubject:') ;
								}
							}
						}
						if(null!=feelem.parent)
						{
							feeditm.FeedFrom = feelem.parent.name;
							if(feelem.parent.id.StartsWith('005'))
							{
								feeditm.FeedSentBy = 'Coach';
							}
							else
							{
								feeditm.FeedSentBy = 'Member';
							}
						}
						feeditm.MessageDateTime = feelem.createdDate;
						
						mapFeedItems.put(feelem.Id, feeditm);
					}
					
				}
			}
		}
		if(null != mapFeedItems && !mapFeedItems.isEmpty())
		{
			getFeedResponse = PopulateFeedItems(mapFeedItems, chatterGetFeedRequest);
		}
		else
		{
			getFeedResponse = new ChatterGetFeedResponseDTO();
			getFeedResponse.Success = true;
			ChatterGetFeedResponseDTO.ChatterFeedReponseDTO getResponse = new ChatterGetFeedResponseDTO.ChatterFeedReponseDTO();
			getResponse.PersonId = chatterGetFeedRequest.PersonId;
			getFeedResponse.ChatterFeedReponse = getResponse;
		}

		return getFeedResponse;

	}
    
    /*
	* Method name : PopulateFeedItems
	* Description : This method is to populate feeditems. 
	* Parameter   : map<string, ChatterGetFeedResponseDTO.ChatterFeedItemsDTO>, ChatterGetFeedRequestDTO
	* ReturnType  : ChatterGetFeedResponseDTO
	*/
	@TestVisible 
    private static ChatterGetFeedResponseDTO PopulateFeedItems(map<string, ChatterGetFeedResponseDTO.ChatterFeedItemsDTO> mapFeedItems, ChatterGetFeedRequestDTO chatterGetFeedRequest)
    {
    	map<string, list<ChatterGetFeedResponseDTO.ChatterFeedCommentsDTO>> mapFeedComments = null;
    	ChatterGetFeedResponseDTO getFeedResponse = new ChatterGetFeedResponseDTO();
		getFeedResponse.Success = true;
		ChatterGetFeedResponseDTO.ChatterFeedReponseDTO getResponse = new ChatterGetFeedResponseDTO.ChatterFeedReponseDTO();
		getResponse.PersonId = chatterGetFeedRequest.PersonId;
		
		//Combile all FeedItemIds to get FeedComments
		set<string> setFeedIemIds = new set<string>();
		setFeedIemIds.addAll(mapFeedItems.keySet());

		mapFeedComments = PopulateFeedComments(setFeedIemIds);
			
		list<ChatterGetFeedResponseDTO.ChatterFeedItemsDTO> feedItemLst = new list<ChatterGetFeedResponseDTO.ChatterFeedItemsDTO>();
		//Populate FeedItem list and FeedComment list from maps
		for(ChatterGetFeedResponseDTO.ChatterFeedItemsDTO feelem: mapFeedItems.values())
		{
			if(null != mapFeedComments && !mapFeedComments.IsEmpty() && mapFeedComments.containsKey(feelem.FeedItemId))
			{
				feelem.FeedCommentList = mapFeedComments.get(feelem.FeedItemId);
			}
			
			feedItemLst.add(feelem);
		}
		unSortedList = feedItemLst;
		//Sort the FeedItems based on date that was retrieved from Search and Get feed
		sortFeedItems();
		getResponse.FeedItemList = sortedList;
		
		getFeedResponse.ChatterFeedReponse = getResponse;
		
		
		return getFeedResponse;
	}
	
	/*
	* Method name : PopulateFeedItem
	* Description : This method is to populate feeditem. 
	* Parameter   : ConnectApi.FeedElement, ChatterGetFeedRequestDTO
	* ReturnType  : ChatterGetFeedResponseDTO
	*/
	@TestVisible 
    private static ChatterGetFeedResponseDTO PopulateFeedItem(ConnectApi.FeedElement feedvalue, ChatterGetFeedRequestDTO chatterGetFeedRequest)
    {
    	map<string, list<ChatterGetFeedResponseDTO.ChatterFeedCommentsDTO>> mapFeedComments = null;
    	ChatterGetFeedResponseDTO getFeedResponse = new ChatterGetFeedResponseDTO();
		getFeedResponse.Success = true;
		ChatterGetFeedResponseDTO.ChatterFeedReponseDTO getResponse = new ChatterGetFeedResponseDTO.ChatterFeedReponseDTO();
		getResponse.PersonId = chatterGetFeedRequest.PersonId;
		set<string> setFeedItemIds = new set<string>{feedvalue.Id};
		mapFeedComments = PopulateFeedComments(setFeedItemIds);
			
		list<ChatterGetFeedResponseDTO.ChatterFeedItemsDTO> feedItemLst = new list<ChatterGetFeedResponseDTO.ChatterFeedItemsDTO>();
		ChatterGetFeedResponseDTO.ChatterFeedItemsDTO feeditm = new ChatterGetFeedResponseDTO.ChatterFeedItemsDTO();
		feeditm.FeedItemId = feedvalue.Id;
		if(null!=feedvalue.body && string.isNotBlank(feedvalue.body.text))
		{
			if(feedvalue.body.text.indexOf(GLOBAL_CONSTANT_CH_HUM.CoachingSubject) != -1)
			{
				feeditm.MessageText = feedvalue.body.text.substringAfter(GLOBAL_CONSTANT_CH_HUM.CoachingSubject);
				if(feeditm.MessageText.indexOf('CarePlanSubject:') != -1)
				{
					feeditm.MessageText = feeditm.MessageText.substringBefore('CarePlanSubject:') ;
				}
			}
			else
			{
				feeditm.MessageText = feedvalue.body.text;
				if(feeditm.MessageText.indexOf('CarePlanSubject:') != -1)
				{
					feeditm.MessageText = feeditm.MessageText.substringBefore('CarePlanSubject:') ;
				}
			}
		}
		if(null!=feedvalue.parent)
		{
			feeditm.FeedFrom = feedvalue.parent.name;
			if(feedvalue.parent.id.StartsWith('005'))
			{
				feeditm.FeedSentBy = 'Coach';
			}
			else
			{
				feeditm.FeedSentBy = 'Member';
			}
		}
		feeditm.MessageDateTime = feedvalue.createdDate;
		if(null != mapFeedComments && !mapFeedComments.IsEmpty() && mapFeedComments.containsKey(feedvalue.ID))
		{
			feeditm.FeedCommentList = mapFeedComments.get(feedvalue.ID);
		}
		feedItemLst.add(feeditm);
		getResponse.FeedItemList = feedItemLst;
		
		getFeedResponse.ChatterFeedReponse = getResponse;
		return getFeedResponse;
    }
    
    /*
	* Method name : PopulateFeedComments
	* Description : This method is to populate FeedComments. 
	* Parameter   : set<string>
	* ReturnType  : map<string, list<ChatterGetFeedResponseDTO.ChatterFeedCommentsDTO>>
	*/
	@TestVisible 
    private static map<string, list<ChatterGetFeedResponseDTO.ChatterFeedCommentsDTO>> PopulateFeedComments(set<string> setFeedIemIds)
    {
    	
    	map<string, list<ChatterGetFeedResponseDTO.ChatterFeedCommentsDTO>> mapFeedCommentList = null;
    	list<FeedComment> feedCommentList = [SELECT Id, FeedItemId, ParentId, CreatedById, CreatedBy.Name, CreatedDate, CommentBody FROM FeedComment where FeedItemId in :setFeedIemIds and CreatedDate >= :dtFromDateTimeGMT and CreatedDate <= :dtToDateTimeGMT order by createddate desc ];
    	if(null != feedCommentList && !feedCommentList.isEmpty())
    	{
			mapFeedCommentList = new map<string, list<ChatterGetFeedResponseDTO.ChatterFeedCommentsDTO>>();
			//Populate FeedComment list
    		for(FeedComment fecmt :feedCommentList)
    		{
				ChatterGetFeedResponseDTO.ChatterFeedCommentsDTO chatterFeedComment = new ChatterGetFeedResponseDTO.ChatterFeedCommentsDTO();
				chatterFeedComment.FeedCommentId = fecmt.Id;
				if(string.isNotBlank(fecmt.CreatedBy.Name) && GLOBAL_CONSTANT_CH_HUM.DatapowerUserName.equalsIgnoreCase(fecmt.CreatedBy.Name))
    			{
    				chatterFeedComment.MessageText = fecmt.CommentBody.SubStringAfter(':-');
    			}
    			else
    			{
    				chatterFeedComment.MessageText = fecmt.CommentBody;
    			}
				//If Created By is Datapower then Parent Name will be the Account Name in the comment.
    			if(string.isNotBlank(fecmt.CreatedBy.Name) && GLOBAL_CONSTANT_CH_HUM.DatapowerUserName.equalsIgnoreCase(fecmt.CreatedBy.Name))
    			{
    				String strParentName = fecmt.CommentBody.SubStringBefore(':-');
					if(string.isNotBlank(strParentName))
					{
    					chatterFeedComment.FeedFrom = strParentName.SubStringAfter('/');
						chatterFeedComment.FeedSentBy = 'Member';
					}
    			}
    			else
    			{
    				chatterFeedComment.FeedFrom = fecmt.CreatedBy.Name;
					chatterFeedComment.FeedSentBy = 'Coach';
    			}
			chatterFeedComment.MessageDateTime = string.valueOfGmt(fecmt.CreatedDate);
    			if(!mapFeedCommentList.containsKey(fecmt.FeedItemId))
                {
                    mapFeedCommentList.put(fecmt.FeedItemId,new list<ChatterGetFeedResponseDTO.ChatterFeedCommentsDTO>{chatterFeedComment});
                }
                else
                {
                    mapFeedCommentList.get(fecmt.FeedItemId).add(chatterFeedComment);
                }
    		}
    	}
    	    	
    	return mapFeedCommentList;
	}
	
	/*
	* Method name : sortFeedItems
	* Description : This method is to sort feeditems from search and get feed. 
	* ReturnType  : void
	*/
	private static void sortFeedItems(){
		integer targetsize = unSortedList.size();

		while(sortedList.size() != targetsize){
			findMinDateValue();
		}

	}

	/*
	* Method name : findMinDateValue
	* Description : This method is to find minimum date from list to sort in descending order. 
	* ReturnType  : void
	*/
  	private static void findMinDateValue(){
		datetime dt = Datetime.newInstance(1900, 12, 31, 00, 00, 00); // for decending

		integer i = 0;
		integer mini = 0;

		for (i = 0; i != unSortedList.size() ; i ++){
			  if(unSortedList[i].MessageDateTime > dt){ // if decending
					dt = unSortedList[i].MessageDateTime;
					mini = i;
			  }
		}
		sortedList.add(unSortedList[mini]);
		unSortedList.remove(mini);
	}
}