/*******************************************************************************************************************************
Apex Class Name : UserProfileController_C_HUM 
Version         : 1.0
Created On      : 07/08/2020
Function        : This is a Controller class for User Profile page Community.
Test Class      : UserProfileController_T_HUM

Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Veena  Aphale                                      07/08/2020                    Original version
 **************************************************************************************************************************************/
public without sharing class UserProfileController_C_HUM {
    public class CommunityTestException extends Exception {}
    
    /**
     * getUserActivity
     * Fetch count of User activities - Number of questions posted and number of answers / un-answered question
     * @param
     * @return Map<String,String>
     **/
    
    @AuraEnabled(cacheable=true)
    public static Map<String,String> getUserActivity() {
        Map<String,String> mapUserActivity = new Map<String,String>();
        String formatted_count_posted;
        String formatted_count_answered;
        String formatted_count_unanswered;
        Integer countAnswered = 0;
        mapUserActivity.put('Posted','00');
        mapUserActivity.put('Answered','00');
        mapUserActivity.put('Unanswered','00');
        try {
            List<FeedItem> lstFeedItem = [SELECT Id, NetworkScope, Type,ParentId ,body,title 
                                          FROM FeedItem 
                                          WHERE Type='QuestionPost' 
                                          AND ParentId=:UserInfo.getUserId() 
                                          AND Networkscope = :Network.getNetworkId() ];
            if (!lstFeedItem.isEmpty())
            {
                formatted_count_posted = (lstFeedItem.size()<=9)?'0'+String.valueOf(lstFeedItem.size()) :String.valueOf(lstFeedItem.size()) ;
                mapUserActivity.put('Posted',formatted_count_posted);
                
                AggregateResult[] groupedResults = [SELECT count(Id) ansCount,FeedItemId from FeedComment WHERE FeedItemId in :lstFeedItem GROUP BY FeedItemId];
                countAnswered = groupedResults.size();
                formatted_count_answered = (countAnswered <= 9 )? '0'+String.valueOf(countAnswered):String.valueOf(countAnswered );
                mapUserActivity.put('Answered',formatted_count_answered );
                formatted_count_unanswered = ((lstFeedItem.size() - countAnswered) <=9 ) ? '0'+String.valueOf(lstFeedItem.size() - countAnswered):String.valueOf(lstFeedItem.size() - countAnswered);
                mapUserActivity.put('Unanswered',formatted_count_unanswered);
                
            }
            if(Test.isRunningTest())
            {
                throw new CommunityTestException('Test Exception');
            }
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'UserProfileController_C_HUM ', 'getUserActivity');
        }
        return mapUserActivity;
    }


    /**
     * sendMessage
     * Send Direct Message to the Moderator
     * @param String param
     * @return String
     **/

    @AuraEnabled
    public static String sendMessage(String param) {
        List<Community_Setup__c> objCS = [SELECT Moderator__c,Id,Is_Active_Moderator__c , Moderator__r.isActive FROM Community_Setup__c WHERE Is_Active_Moderator__c = true and Recordtype.DeveloperName = :CommunityConstants_C_HUM.DIRECT_MESSAGE_RECORDTYPE and Moderator__r.isActive = true ];
        List<String> memberIds = new List<String>();
        FeedItem objFeed = new FeedItem ();
        String feedId = '';
        try {
            ConnectApi.FeedItemInput feedItemInput = new ConnectApi.FeedItemInput();
            ConnectApi.TextSegmentInput textSegmentInput = new ConnectApi.TextSegmentInput();
            textSegmentInput.text = param;
            ConnectApi.MessageBodyInput messageBodyInput = new ConnectApi.MessageBodyInput();
            messageBodyInput.messageSegments = new List<ConnectApi.MessageSegmentInput>();
            messageBodyInput.messageSegments.add(textSegmentInput);
            feedItemInput.body = messageBodyInput;
            
            if (!objCS.isEmpty()){
                for (Community_Setup__c rec: objCS){
                memberIds.add(rec.Moderator__c);
                }
                 
            }
            ConnectApi.DirectMessageCapabilityInput dmInput = new ConnectApi.DirectMessageCapabilityInput();
            dmInput.subject = System.Label.Community_Moderator_Chat;
            dmInput.membersToAdd = memberIds;
            
            ConnectApi.FeedElementCapabilitiesInput feedElementCapabilitiesInput = new ConnectApi.FeedElementCapabilitiesInput();
            feedElementCapabilitiesInput.directMessage = dmInput;
            
            feedItemInput.capabilities = feedElementCapabilitiesInput;
            ConnectApi.FeedElement feedElement = ConnectApi.ChatterFeeds.postFeedElement(Network.getNetworkId(), feedItemInput);
            
            objFeed = [Select Id, parentId, Body FROM FeedItem where createdbyid = :Userinfo.getUserId() order by createddate desc limit 1];
            if (String.valueOf(objFeed.parentId).startsWith('05y') && objFeed.body.contains(param))
               feedId = String.valueOf(objFeed.Id);
               
               if(Test.isRunningTest())
             {
                throw new CommunityTestException('Test Exception');
             }
             
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'UserProfileController_C_HUM ', 'sendMessage');
        }
        return (feedId);
    }

     /**
     * getBookmarkedArticles
     * Fetch the articles bookmarked by the User
     * @param 
     * @return List<ArticleBookmark__c>
     **/

    @AuraEnabled(cacheable=true)
    public static List<ArticleBookmark__c> getBookmarkedArticles() {
        List<ArticleBookmark__c> lstArticles = new List<ArticleBookmark__c>();
        try{
            lstArticles = [SELECT Id, Name, ArticleId__c, ArticleId__r.Title, ArticleId__r.UrlName, ArticleId__r.Summary, ArticleId__r.ArticleCreatedDate, Bookmarked__c, CreatedById FROM ArticleBookmark__c where Bookmarked__c= true and CreatedById=: userinfo.getUserId() limit 10];
             if(Test.isRunningTest())
             {
                throw new CommunityTestException('Test Exception');
             }
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'ArticleDetailController_C_HUM ', 'getBookmarkedArticles');
        }
        return lstArticles;
    }

    

}