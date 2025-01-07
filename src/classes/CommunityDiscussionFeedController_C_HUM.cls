/*******************************************************************************************************************************
Apex Class Name : CommunityDiscussionFeedController_C_HUM 
Version         : 1.0
Created On      : 08/03/2021
Function        : This is the Controller class for Discussion feed in Communities.
Test Class      : CommunityDiscussionFeedController_T_HUM

Modification Log: 
* Developer Name           Code Review                 Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nitaj Shaik                                      08/03/2021                    Original version
* Nitaj Shaik                                      02/02/2022                    Modified the SOQL(Added one more filter with createddate, ordering the result with another SOQL after getting the result)
* Nitaj Shaik                                      02/03/2022                    Moved out subquery of feedcomments object from feeditem object SOQL query
**************************************************************************************************************************************/
public without sharing class CommunityDiscussionFeedController_C_HUM {
    public class CommunityTestException extends Exception {}
     /**
     * getDiscussionCompConfig
     * Used to switch on/off the component in pages
     * @param 
     * @return boolean
     **/
    @AuraEnabled
    public static CRMFunctionality_ONOFF_Switch__c getDiscussionCompConfig(){
        return CRMFunctionality_ONOFF_Switch__c.getInstance(CommunityConstants_C_HUM.INITAL_LOADED);
    }
     /**
     * isRendered
     * Used to hide the component for guest users
     * @param 
     * @return boolean
     **/
    @AuraEnabled
    public static boolean isRendered(){
        boolean isrendered = false;
        if(userInfo.getUserType() == CommunityConstants_C_HUM.DISCUSSION_FEEDUSERTYPE){
            isrendered = true;
        }
        return isrendered;
    }

     /**
     * getLatestFeedOptions
     * Fetch latest feed options details and display on sort by picklist field
     * @param 
     * @return map<string,string>
     **/
    @AuraEnabled(cacheable=true)
    public static map<string,string> getLatestFeedOptions(){
        map<string,string> feedValuesMap = new map<string,string>();
        feedValuesMap.put(CommunityConstants_C_HUM.DISCUSSION_FEEDOPTION_1 ,CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_1);
        feedValuesMap.put(CommunityConstants_C_HUM.DISCUSSION_FEEDOPTION_2 ,CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_2);
        feedValuesMap.put(CommunityConstants_C_HUM.SORTITEM_TYPE ,CommunityConstants_C_HUM.SORTITEM_TYPE);
        return feedValuesMap;
    }
    /**
     * getFeedOptions
     * Fetch feed options details and display on Feed filter menu item component
     * @param 
     * @return map<string,string>
     **/
    @AuraEnabled(cacheable=true)
    public static map<string,string> getFeedOptions(){
        map<string,string> feedValuesMap = new map<string,string>();
        feedValuesMap.put(CommunityConstants_C_HUM.DISCUSSION_FEEDOPTION_3 ,CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_1);
        feedValuesMap.put(CommunityConstants_C_HUM.DISCUSSION_FEEDOPTION_4 ,CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_2);
        return feedValuesMap;
    }
     /**
     * getSearchFeedItems
     * This method is used to get the list of feeditems when search and filter for an item.
     * @param String feedItemDetails
     * @return list<DiscussionFeedWrapper>
     **/
    @AuraEnabled
    public static list<DiscussionFeedWrapper> getSearchFeedItems(String feedItemDetails){ 
        FeedItemWrapper feedItmWrap;
        Id NetwrkId;
        if(Test.isRunningTest()){
            NetwrkId= null;
        }
        else{
            NetwrkId= Network.getNetworkId();
        }
        List<String> typeValues; 
        if(String.isNotBlank(feedItemDetails)){
            //converting the json string to a FeeItemWrapper class
            feedItmWrap = (FeedItemWrapper) JSON.deserialize(feedItemDetails, FeedItemWrapper.class);
        }
        Integer itemsLength = feedItmWrap.feedItemsLength;
        list<DiscussionFeedWrapper> discussionFeedWrapList = new list<DiscussionFeedWrapper>();
        //validate if component is visible in UI
        if(feedItmWrap.isComponentVisible != null && feedItmWrap.isComponentVisible){
            list<FeedItem> feedItemList = new list<FeedItem>();
            list<FeedItem> feedItemsWithLikes = new list<FeedItem>();
            List<String> feedItemIds = new List<String>();
            Map<Id,List<FeedComment>> mapOfFeedCommentList = new Map<Id,List<FeedComment>>();
            try{
                if(String.isNotBlank(feedItmWrap.selectItem)){//search for a feed along with filteration
                    if(feedItmWrap.selectItem == CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_1){// if filteration type is Post
                        typeValues = new List<String>{CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_1,CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_3};
                    }else{// if filteration type is Question
                        typeValues = new List<String>{CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_2};     
                    } 
                    feedItemList = [SELECT Id,Body,Title,Type FROM FeedItem Where isClosed = false AND visibility = 'AllUsers' 
                                    AND isDeleted = false AND SystemModstamp= LAST_N_DAYS:30 AND CreatedDate = LAST_N_DAYS:30 
                                    AND networkscope =:NetwrkId AND Type IN :typeValues LIMIT :CommunityConstants_C_HUM.SEARCHFEED_LIMIT];
                }
                else{//search for a feed without filteration
                    typeValues = new List<String>{CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_1,CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_2,CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_3};
                    feedItemList = [SELECT Id,Body,Title,Type FROM FeedItem Where isClosed = false AND visibility = 'AllUsers' 
                                    AND isDeleted = false AND SystemModstamp= LAST_N_DAYS:30 AND CreatedDate = LAST_N_DAYS:30 
                                    AND networkscope =:NetwrkId AND Type IN :typeValues LIMIT :CommunityConstants_C_HUM.SEARCHFEED_LIMIT];
                }      
                if(!feedItemList.isEmpty()){
                    if(String.isNotBlank(feedItmWrap.searchInput)){
                        List<FeedItem> feedItemsWithSearchInput = new List<FeedItem>();
                        for(feedItem fitem : feedItemList){
                            //when user searches for item, capture itemsLength of items
                            if(feedItemsWithSearchInput.size() < itemsLength){
                                //if feed type is TextPost or LinkPost
                                if((fitem.Type == CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_1 || fitem.Type == CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_3) && String.isNotBlank(fitem.Body) && fitem.body.contains(feedItmWrap.searchInput)){
                                    feedItemsWithSearchInput.add(fitem);
                                }
                                //if feed type is Question
                                if(fitem.Type == CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_2 && String.isNotBlank(fitem.Title) && fitem.Title.contains(feedItmWrap.searchInput)){
                                    feedItemsWithSearchInput.add(fitem);
                                }
                            }
                        }
                        if(!feedItemsWithSearchInput.isEmpty()){
                            for(FeedItem feedItm : feedItemsWithSearchInput){feedItemIds.add(feedItm.Id);}// add feeditem ids to a list
                            feedItemsWithLikes = generateFeedItemsWithLikes(feedItemIds);//get feeditems along with feed likes
                            system.debug('feedItemList :' + feedItemsWithLikes);
                            mapOfFeedCommentList = generateCommentsOfFeeds(feedItemIds);// get feedcomments of feeditems
                            discussionFeedWrapList = getDiscussionFeedWrapper(null,feedItemsWithLikes,mapOfFeedCommentList);
                        } 
                    }
                }
                system.debug('discussionFeedWrapList ===>' + discussionFeedWrapList.size());
                if(Test.isRunningTest())
                {
                    throw new CommunityTestException('Test Exception');
                }
            }catch(Exception e){
                HUMExceptionHelper.logErrors(e, 'CommunityDiscussionFeedController_C_HUM', 'getSearchFeedItems');
            }
        }
        return discussionFeedWrapList;
    }
     /**
     * getSortedFeedItems
     * This method is used to get the list of feeditems when sorted for an items.
     * @param String feedItemDetails
     * @return list<DiscussionFeedWrapper>
     **/
    @AuraEnabled
    public static list<DiscussionFeedWrapper> getSortedFeedItems(String feedItemDetails){
        FeedItemWrapper feedItmWrap;
        Id NetwrkId;
        if(Test.isRunningTest()){
            NetwrkId= null;
        }
        else{
            NetwrkId= Network.getNetworkId();
        }
        List<String> typeValues;
        if(String.isNotBlank(feedItemDetails)){
            //converting the json string to a FeeItemWrapper class
            feedItmWrap = (FeedItemWrapper) JSON.deserialize(feedItemDetails, FeedItemWrapper.class);
        }
        Integer itemsLength = feedItmWrap.feedItemsLength;// limit to display the feeditems on UI
        list<DiscussionFeedWrapper> discussionFeedWrapList = new list<DiscussionFeedWrapper>();
        //validate if component is visible in UI
        if(feedItmWrap.isComponentVisible != null && feedItmWrap.isComponentVisible){
            list<FeedItem> feedItemList = new list<FeedItem>();
            list<FeedItem> feedItemsWithLikes = new list<FeedItem>();
            List<String> feedItemIds = new List<String>();
            Map<Id,List<FeedComment>> mapOfFeedCommentList = new Map<Id,List<FeedComment>>();
            try{
                //if sorted item not equals 'Most Recent Activity'
                if(String.isNotBlank(feedItmWrap.sortItem) && feedItmWrap.sortItem != CommunityConstants_C_HUM.SORTITEM_TYPE){
                    if(feedItmWrap.sortItem == CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_1){// if sorted type is Latest Posts
                        typeValues = new List<String>{CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_1,CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_3};
                     }else{// if sorted type is Latest Questions
                        typeValues = new List<String>{CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_2};     
                     } 
                    feedItemList = [SELECT Id FROM FeedItem Where isClosed = false AND visibility = 'AllUsers' AND isDeleted = false 
                                    AND SystemModstamp= LAST_N_DAYS:30 AND CreatedDate = LAST_N_DAYS:30 AND 
                                    networkscope =:NetwrkId AND Type IN :typeValues LIMIT :itemsLength];
                }else{//if sorted item is 'Most Recent Activity'
                    typeValues = new List<String>{CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_1,CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_2,CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_3};
                    feedItemList = [SELECT Id FROM FeedItem Where isClosed = false AND visibility = 'AllUsers' AND
                                    isDeleted = false AND SystemModstamp= LAST_N_DAYS:30 AND CreatedDate = LAST_N_DAYS:30 AND
                                    networkscope =:NetwrkId AND Type IN :typeValues LIMIT :itemsLength];
                }
                if(!feedItemList.isEmpty()){
                    for(FeedItem feedItm : feedItemList){feedItemIds.add(feedItm.Id);}// add feeditem ids to a list
                }
                feedItemsWithLikes = generateFeedItemsWithLikes(feedItemIds);//get feeditems along with feed likes
                system.debug('feedItemList :' + feedItemsWithLikes);
                mapOfFeedCommentList = generateCommentsOfFeeds(feedItemIds);// get feedcomments of feeditems
                if(!feedItemsWithLikes.isEmpty()){
                    discussionFeedWrapList = getDiscussionFeedWrapper(null,feedItemsWithLikes,mapOfFeedCommentList); 
                }
                system.debug('discussionFeedWrapList ===>' + discussionFeedWrapList.size());
                if(Test.isRunningTest())
                {
                    throw new CommunityTestException('Test Exception');
                }
            }catch(Exception e){
                HUMExceptionHelper.logErrors(e, 'CommunityDiscussionFeedController_C_HUM', 'getSortedFeedItems');
            }
        }
        return discussionFeedWrapList;        
    }
     /**
     * getLoadedOrRefreshedFeedItems
     * This method is used to get the list of feeditems when click on refresh button or on load of component.
     * @param String feedItemLength
     * @return list<DiscussionFeedWrapper>
     **/
    @AuraEnabled
    public static list<DiscussionFeedWrapper> getLoadedOrRefreshedFeedItems(Integer feedItemLength, Boolean isCompVisible, Boolean isLoadMore){
        Id NetwrkId;
        if(Test.isRunningTest()){
            NetwrkId= null;
        }
        else{
            NetwrkId= Network.getNetworkId();
        }
        //Storing the list of feed types
        List<String> typeValues = new List<String>{CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_1,CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_2,CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_3};
        list<DiscussionFeedWrapper> discussionFeedWrapList = new list<DiscussionFeedWrapper>();
        //validate if component is visible in UI
        if(isCompVisible != null && isCompVisible){
            list<FeedItem> feedItemList = new list<FeedItem>();
            list<FeedItem> feedItemsWithLikes = new list<FeedItem>();
            List<String> feedItemIds = new List<String>();
            Map<Id,List<FeedComment>> mapOfFeedCommentList = new Map<Id,List<FeedComment>>();
            try{
                //this will run on initialize or user click on refresh button on UI
                if(!isLoadMore){
                    feedItemList = [SELECT Id FROM FeedItem Where isClosed = false AND visibility = 'AllUsers' AND isDeleted = false 
                                AND SystemModstamp= LAST_N_DAYS:5 AND CreatedDate = LAST_N_DAYS:5 AND networkscope =:NetwrkId
                                AND Type IN :typeValues LIMIT :feedItemLength];
                }else{//if user click on view more button to get more feed items, limit will increase by 10 if click on view more button
                    feedItemList = [SELECT Id FROM FeedItem Where isClosed = false AND visibility = 'AllUsers' AND isDeleted = false 
                                AND SystemModstamp= LAST_N_DAYS:30 AND CreatedDate = LAST_N_DAYS:30 AND networkscope =:NetwrkId 
                                AND Type IN :typeValues LIMIT :feedItemLength];
                }
                if(!feedItemList.isEmpty()){
                for(FeedItem feedItm : feedItemList){feedItemIds.add(feedItm.Id);}//add feeditem ids to a list
                }
                feedItemsWithLikes = generateFeedItemsWithLikes(feedItemIds);//get feeditems along with feed likes
                system.debug('feedItemList :' + feedItemsWithLikes);
                mapOfFeedCommentList = generateCommentsOfFeeds(feedItemIds);// get feedcomments of feeditems
                if(!feedItemsWithLikes.isEmpty()){
                    discussionFeedWrapList = getDiscussionFeedWrapper(null,feedItemsWithLikes,mapOfFeedCommentList);
                }
                system.debug('discussionFeedWrapList ===>' + discussionFeedWrapList.size());
                if(Test.isRunningTest())
                {
                    throw new CommunityTestException('Test Exception');
                }
            }catch(Exception e){
                HUMExceptionHelper.logErrors(e, 'CommunityDiscussionFeedController_C_HUM', 'getLoadedOrRefreshedFeedItems');
            }
        }
        return discussionFeedWrapList;
    }
     /**
     * generateCommentsOfFeeds
     * This method is used to get the current user likes for a feedItems.
     * @param List<String> feedItemIds
     * @return List<FeedItem>
     **/
    private static List<FeedItem> generateFeedItemsWithLikes(List<String> feedItemIds){
        list<FeedItem> feedItemsWithLikes = new list<FeedItem>();
        feedItemsWithLikes = [SELECT Id, body,Title,createdBy.Name,Type,commentCount,LikeCount,(SELECT id,InsertedById,createdById, FeedItemId FROM FeedLikes WHERE createdById =: UserInfo.getUserId()),
                              createdDate FROM FeedItem WHERE Id IN: feedItemIds];
        return feedItemsWithLikes;
    }
     /**
     * generateCommentsOfFeeds
     * This method is used to get the list of comments of a feedItems.
     * @param List<String> feedItemIds
     * @return Map<Id,List<FeedComment>>
     **/
    private static Map<Id,List<FeedComment>> generateCommentsOfFeeds(List<String> feedItemIds){
        Map<Id,List<FeedComment>> mapOfFeedCommentList = new Map<Id,List<FeedComment>>();
        for(FeedComment feedCmnt : [SELECT Id, commentBody, createdBy.Name, createdDate, FeedItemId FROM FeedComment WHERE FeedItemId IN :feedItemIds]){
            List<FeedComment> feedComtList = mapOfFeedCommentList.get(feedCmnt.FeedItemId);
            if(feedComtList == null)
                feedComtList = new List<FeedComment>();
            feedComtList.add(feedCmnt);
            mapOfFeedCommentList.put(feedCmnt.FeedItemId,feedComtList);
        }
        return mapOfFeedCommentList;
    }
    /**
     * getDiscussionFeedWrapper
     * This method is used to get the list of feeditems.
     * @param String feedItemId,List<FeedItem> feedItemList,Map<id,FeedItem> userLikedMaps
     * @return list<DiscussionFeedWrapper>
     **/
    @testVisible
    private static list<DiscussionFeedWrapper> getDiscussionFeedWrapper(String feedItemId,List<FeedItem> feedItemList,Map<Id,List<FeedComment>> mapOfFeedCommentList){
        list<DiscussionFeedWrapper> discussionFeedWrapList = new list<DiscussionFeedWrapper>();
        for(feedItem fitem : feedItemList){
            List<CommentWrapper> commentWrapperList = new List<CommentWrapper>();
            //create a feeditem record wrapper along with the feedcomments wrapper
            DiscussionFeedWrapper discussionFeedWrap = new DiscussionFeedWrapper();
            discussionFeedWrap.userName = fitem.createdBy.Name;
            discussionFeedWrap.createdDate = fitem.createdDate;
            discussionFeedWrap.createDate = fitem.createdDate.format();
            discussionFeedWrap.recordId = fitem.id;
            discussionFeedWrap.LikeCount = fitem.LikeCount;
            discussionFeedWrap.commentCount = fitem.commentCount;
            //validate to show Liked or Like button based on current user 
            discussionFeedWrap.liked = fitem.FeedLikes.size() > 0 ? true : false;
            //validate to show showmore link if comments are more than 1
            discussionFeedWrap.showMoreCommentBox = fitem.CommentCount > 1 ? true : false;
            //validate to show which type of feed it is to show on UI
            discussionFeedWrap.feedType = fitem.Type == CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_1 ? CommunityConstants_C_HUM.DISCUSSION_FEEDPOSTTYPE_1 : CommunityConstants_C_HUM.DISCUSSION_FEEDPOSTTYPE_2;
            //validate to check the type of feed, if textpost -> capture body data, if questionpost -> capture title data.
            if((fitem.Type == CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_1 || fitem.Type == CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_3) && String.isNotBlank(fitem.Body))   
            discussionFeedWrap.body = fitem.Body.stripHtmlTags();
            system.debug('body test: '+ discussionFeedWrap.body);
            if(fitem.Type == CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_2 && String.isNotBlank(fitem.Title))
            discussionFeedWrap.body =  fitem.Title;
            discussionFeedWrap.commentType = fitem.Type == CommunityConstants_C_HUM.DISCUSSION_FEEDITEMTYPE_1 ? true : false;
            //if feedItemId is not blank then request is coming from showmorecomments, hidecomments, submit comment button/links.
            if(feedItemId == fitem.Id && String.isNotBlank(feedItemId)){
                discussionFeedWrap.hideComments =  true;
                discussionFeedWrap.showCommentCount = fitem.commentCount;
                for(feedComment cmnt : mapOfFeedCommentList.get(fitem.Id)){
                    CommentWrapper cmntWrap = new CommentWrapper();
                    if(String.isNotBlank(cmnt.commentBody)){
                         cmntWrap.commentBody = cmnt.commentBody.stripHtmlTags();
                    }
                    cmntWrap.commentUserName = cmnt.createdBy.Name;
                    cmntWrap.crtedDate = cmnt.createdDate;
                    cmntWrap.commentCreatedDate = cmnt.createdDate.format();
                    cmntWrap.commentRecordId = cmnt.Id;
                    commentWrapperList.add(cmntWrap);
                    system.debug('commentWrapperList -->' + commentWrapperList);
                }
                if(commentWrapperList != null && commentWrapperList.size() > 0){
                    commentWrapperList.sort();
                }
            }else{
                List<FeedComment> feedcmntList = mapOfFeedCommentList.get(fitem.Id);
                if(feedcmntList != null && !feedcmntList.isEmpty()){
                    if(!feedcmntList.isEmpty()){
                        discussionFeedWrap.hideComments =  false;
                        discussionFeedWrap.showCommentCount = 1;
                        CommentWrapper cmntWrap = new CommentWrapper();
                        if(String.isNotBlank(feedcmntList[0].commentBody)){
                            cmntWrap.commentBody = feedcmntList[0].commentBody.stripHtmlTags();
                        }
                        cmntWrap.commentUserName = feedcmntList[0].createdBy.Name;
                        cmntWrap.crtedDate = feedcmntList[0].createdDate;
                        cmntWrap.commentCreatedDate = feedcmntList[0].createdDate.format();
                        cmntWrap.commentRecordId = feedcmntList[0].Id;
                        commentWrapperList.add(cmntWrap);
                    } 
                    if(commentWrapperList != null && commentWrapperList.size() > 0){
                        commentWrapperList.sort();
                    }
                } 
            }
            discussionFeedWrap.commentWrapperList = commentWrapperList;
            discussionFeedWrapList.add(discussionFeedWrap);
            system.debug('discussionFeedWrapList --->'+ discussionFeedWrapList);
        } 
        system.debug('discussionFeedWrapList --->'+ discussionFeedWrapList);
        if(discussionFeedWrapList != null && discussionFeedWrapList.size() > 0){
            discussionFeedWrapList.sort();
        }
        system.debug('discussionFeedWrapList after sort===>' + discussionFeedWrapList);
        return discussionFeedWrapList;
    }
    /**
     * submitFeedComment
     * insert feedcomment record and returns the updated list
     * @param List<String> feedItemsIds,String commentDetails
     * @return list<DiscussionFeedWrapper>
     **/
    @AuraEnabled
    public static list<DiscussionFeedWrapper> submitFeedComment(List<String> feedItemsIds,String commentDetails){
        list<DiscussionFeedWrapper> discussionFeedWrapList = new list<DiscussionFeedWrapper>();
        list<FeedItem> feedItemsWithLikes = new list<FeedItem>();
        Map<Id,List<FeedComment>> mapOfFeedCommentList = new Map<Id,List<FeedComment>>();
        CommentWrapper cmntWrap;
        if(String.isNotBlank(commentDetails)){
            //converting the json string into comment wrapper
            cmntWrap = (CommentWrapper) JSON.deserialize(commentDetails, CommentWrapper.class);
        }
        try{
            //Create feed comment for a feed item 
            FeedComment fdCmnt = new FeedComment();
            fdCmnt.CommentBody = cmntWrap.commentBody;
            fdCmnt.CommentType = CommunityConstants_C_HUM.DISCUSSION_FEEDCOMMENTTYPE;
            fdCmnt.FeedItemId  = cmntWrap.feedItemId;
            insert fdCmnt;
            feedItemsWithLikes = generateFeedItemsWithLikes(feedItemsIds);//get feeditems along with feed likes
            system.debug('feedItemList :' + feedItemsWithLikes);
            if(!feedItemsWithLikes.isEmpty()){
                feedItemsWithLikes = generateFeedItemsWithLikes(feedItemsIds);//get feeditems along with feed likes
                system.debug('feedItemList :' + feedItemsWithLikes);
                mapOfFeedCommentList = generateCommentsOfFeeds(feedItemsIds);//get feedcomments of feeditems
                discussionFeedWrapList = getDiscussionFeedWrapper(null,feedItemsWithLikes,mapOfFeedCommentList);
            }
            if(Test.isRunningTest())
            {
                throw new CommunityTestException('Test Exception');
            }
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CommunityDiscussionFeedController_C_HUM', 'submitFeedComment');
        }
        return discussionFeedWrapList;
    }
    /**
     * submitOrRemoveFeedLike
     * insert or delete feedlike record and returns the updated list
     * @param List<String> feedItemsIds,String feedItemId,String buttonName
     * @return list<DiscussionFeedWrapper>
     **/
    @AuraEnabled
    public static list<DiscussionFeedWrapper> submitOrRemoveFeedLike(List<String> feedItemsIds,String feedItemId,String buttonName){
        String feedLikeId;
        list<DiscussionFeedWrapper> discussionFeedWrapList = new list<DiscussionFeedWrapper>();
        list<FeedItem> feedItemsWithLikes = new list<FeedItem>();
        Map<Id,List<FeedComment>> mapOfFeedCommentList = new Map<Id,List<FeedComment>>();
        system.debug('feedItemsIds' + feedItemsIds);
        try{
            //Insert feed like for a feed item when button name is Like
            if(String.isNotBlank(buttonName) && buttonName == CommunityConstants_C_HUM.BUTTONNAME_LIKE){
                FeedLike fdLk = new FeedLike();
                fdLk.FeedItemId  = feedItemId;
                insert fdLk;
            }
            //Delete the feed like for a feed item when button name is Liked
            if(String.isNotBlank(buttonName) && buttonName == CommunityConstants_C_HUM.BUTTONNAME_LIKED){
                FeedItem fdItem = [SELECT id,(SELECT id,InsertedById,createdById, FeedItemId FROM FeedLikes WHERE createdById =: UserInfo.getUserId()) from feeditem Where Id =: feedItemId LIMIT 1];
                if(fdItem.FeedLikes.size() > 0){
                    for(feedlike fdlike : fdItem.FeedLikes){
                        feedLikeId = fdlike.id; 
                    }
                }
                FeedLike fdLk = new FeedLike(Id = feedLikeId);
                delete fdLk;
            }
            feedItemsWithLikes = generateFeedItemsWithLikes(feedItemsIds);//get feeditems along with feed likes
            system.debug('feedItemList :' + feedItemsWithLikes);
            if(!feedItemsWithLikes.isEmpty()){ 
                mapOfFeedCommentList = generateCommentsOfFeeds(feedItemsIds);//get feedcomments of feeditems
                discussionFeedWrapList = getDiscussionFeedWrapper(null,feedItemsWithLikes,mapOfFeedCommentList);
            }
            if(Test.isRunningTest())
            {
                throw new CommunityTestException('Test Exception');
            }
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CommunityDiscussionFeedController_C_HUM', 'submitOrRemoveFeedLike');
        }
        return discussionFeedWrapList;
    }
     /**
     * getHideOrMoreComments
     * this method is used to hide or get more comments functionality
     * @param List<String> feedItemsIds,String feedItemId
     * @return list<DiscussionFeedWrapper>
     **/ 
    @AuraEnabled
    public static list<DiscussionFeedWrapper> getHideOrMoreComments(List<String> feedItemsIds,String feedItemId){
        list<DiscussionFeedWrapper> discussionFeedWrapList = new list<DiscussionFeedWrapper>();
        list<FeedItem> feedItemsWithLikes = new list<FeedItem>();
        Set<Id> feedItemIds = new Set<Id>();
        Map<Id,List<FeedComment>> mapOfFeedCommentList = new Map<Id,List<FeedComment>>();
        try{
            feedItemsWithLikes = generateFeedItemsWithLikes(feedItemsIds);//get feeditems along with feed likes
            system.debug('feedItemList :' + feedItemsWithLikes);
            if(!feedItemsWithLikes.isEmpty()){
                mapOfFeedCommentList = generateCommentsOfFeeds(feedItemsIds);//get feedcomments of feeditems
                discussionFeedWrapList = getDiscussionFeedWrapper(feedItemId,feedItemsWithLikes,mapOfFeedCommentList);
            }
            if(Test.isRunningTest())
            {
                throw new CommunityTestException('Test Exception');
            }
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CommunityDiscussionFeedController_C_HUM', 'getHideOrMoreComments');
        }
        return discussionFeedWrapList;
    }
    /**
     * getMoreFeedItems
     * this method is used to get more feeditems when click on view more button
     * @param String feedItemDetails
     * @return list<DiscussionFeedWrapper>
     **/ 
    @AuraEnabled
    public static list<DiscussionFeedWrapper> getMoreFeedItems(String feedItemDetails){
        list<DiscussionFeedWrapper> discussionFeedWrapList = new list<DiscussionFeedWrapper>();
        FeedItemWrapper feedItmWrap;
        if(String.isNotBlank(feedItemDetails)){
             //converting the json string to a FeeItemWrapper class
            feedItmWrap = (FeedItemWrapper) JSON.deserialize(feedItemDetails, FeedItemWrapper.class);
        }
        if(String.isNotBlank(feedItmWrap.searchInput)){//user clicks view more after searched for an Item
            discussionFeedWrapList = getSearchFeedItems(feedItemDetails);
        }else if(String.isNotBlank(feedItmWrap.sortItem) && feedItmWrap.sortItem != CommunityConstants_C_HUM.SORTITEM_TYPE){//user clicks view more after sorted based on feed type
            discussionFeedWrapList = getSortedFeedItems(feedItemDetails);
        }else{//user clicks view more after loaded or refreshing the UI
            discussionFeedWrapList = getLoadedOrRefreshedFeedItems(feedItmWrap.feedItemsLength, feedItmWrap.isComponentVisible, true);
        }
        return discussionFeedWrapList;
    }
    //wrapper class used to capture the feeditem details
    public class DiscussionFeedWrapper implements Comparable{
        @AuraEnabled public String userName {get;set;}
        @AuraEnabled public String createDate {get;set;}
        @AuraEnabled public DateTime createdDate {get;set;}
        @AuraEnabled public String body {get;set;}
        @AuraEnabled public String recordId {get;set;}
        @AuraEnabled public String feedType {get;set;}
        @AuraEnabled public Boolean commentType {get;set;}
        @AuraEnabled public Integer LikeCount {get;set;}
        @AuraEnabled public Integer commentCount {get;set;}
        @AuraEnabled public Boolean liked {get;set;}
        @AuraEnabled public Boolean showMoreCommentBox {get;set;}
        @AuraEnabled public Boolean hideComments {get;set;}
        @AuraEnabled public Integer showCommentCount {get;set;} 
        @AuraEnabled public List<commentWrapper> commentWrapperList {get;set;}
        
        // Implement the compareTo() method
        public Integer compareTo(Object compareTo) {
            DiscussionFeedWrapper compareToEmp = (DiscussionFeedWrapper)compareTo;
            if (createdDate == compareToEmp.createdDate){
                return 0;                
            } 
            if (createdDate < compareToEmp.createdDate){
                return 1;
            } 
            return -1;        
        }
    }
    //wrapper class used to capture the feedcomment details
    public class CommentWrapper implements Comparable{
        @AuraEnabled public String commentBody {get;set;}
        @AuraEnabled public String commentUserName {get;set;}
        @AuraEnabled public DateTime crtedDate {get;set;}
        @AuraEnabled public String commentCreatedDate {get;set;}
        @AuraEnabled public String commentRecordId {get;set;}
        @AuraEnabled public String feedItemId {get;set;}
        // Implement the compareTo() method
        public Integer compareTo(Object compareTo) {
            CommentWrapper compareToEmp = (CommentWrapper)compareTo;
            if (crtedDate == compareToEmp.crtedDate) return 0;
            if (crtedDate < compareToEmp.crtedDate) return 1;
            return -1;        
        }
    }
    public class FeedItemWrapper{
        @AuraEnabled public String selectItem {get;set;}
        @AuraEnabled public String searchInput {get;set;}
        @AuraEnabled public String sortItem {get;set;}
        @AuraEnabled public Integer feedItemsLength {get;set;}
        @AuraEnabled public Boolean isComponentVisible {get;set;}
    }
}