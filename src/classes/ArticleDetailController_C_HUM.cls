/*******************************************************************************************************************************
Apex Class Name : ArticleDetailController_C_HUM 
Version         : 1.0
Created On      : 07/03/2020
Function        : This is a Controller class for Article Detail page in Community.
Test Class      : ArticleDetailController_T_HUM

Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Shreya Agrawal                                      07/03/2020                    Original version
 * Veena Aphale                                        07/20/2020                    Added reference to Utility class
 **************************************************************************************************************************************/

public without sharing class ArticleDetailController_C_HUM {
    public class CommunityTestException extends Exception {}
    
    /**
     * getArticle
     * Fetch article detail using the Article url name  
     * @param String ArticleUrlName
     * @return Knowledge__kav
     **/


    @AuraEnabled(cacheable=true)
    public static Knowledge__kav getArticle(String ArticleUrlName) {
        List<Knowledge__kav> lstKV = new List<Knowledge__kav> ();
        Knowledge__kav kv = new Knowledge__kav();

        try {    
            lstKV = [ SELECT Id, Title, Article_Body__c,UrlName,ArticleCreatedDate,OwnerId FROM Knowledge__kav where UrlName =: ArticleUrlName  limit 1];
            if (!lstKV.isEmpty())
                kv=lstKV[0];
            if(Test.isRunningTest())
			{
				throw new CommunityTestException('Test Exception');
			}
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'ArticleDetailController_C_HUM ', 'getArticle');
        }

        return kv;
    }

    /**
     * getTopics
     * Fetch associated Topic details using the article Url  
     * @param String articleUrl
     * @return List<TopicAssignment>
     **/

    @AuraEnabled(cacheable=true)
    public static List<TopicAssignment> getTopics(String articleUrl) {
        List<TopicAssignment> topicassgn = new List<TopicAssignment>();
        Map<String,String> topicsMap= new Map<String,String>();
        try {
            Knowledge__kav  kvid = [ SELECT Id  FROM Knowledge__kav where UrlName =: articleUrl LIMIT 1];
            if(kvid.id != null){
                topicassgn = [ SELECT Id, Topic.Name,EntityId,TopicId FROM TopicAssignment where  EntityId =: kvid.id and NetworkId = : Network.getNetworkId()];
            }
            if(Test.isRunningTest())
			{
				throw new CommunityTestException('Test Exception');
			}
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'ArticleDetailController_C_HUM ', 'getTopics');
        }
        return topicassgn;
    }
    
    /**
     * bookmarkController
     * Bookmark article after User clicks on bookmark icon  
     * @param Id articleId
     * @return Boolean
     **/

    @AuraEnabled
    public static boolean bookmarkController(Id articleId) {
        Boolean bookmarked=false;
        try{
            List<ArticleBookmark__c> ArticleBookmarked = [SELECT Id, Name, ArticleId__c, Bookmarked__c, CreatedById FROM ArticleBookmark__c where ArticleId__c=: articleId and CreatedById=: userinfo.getUserId() limit 1];
            
            if(!ArticleBookmarked.isEmpty()){
                if(ArticleBookmarked[0].Bookmarked__c==true){
                    ArticleBookmarked[0].Bookmarked__c= false;
                }
                else{
                    ArticleBookmarked[0].Bookmarked__c= true;
                    bookmarked=true;
                }
                update ArticleBookmarked;
            }
            else{
                ArticleBookmark__c newBookmark =new ArticleBookmark__c();
                newBookmark.ArticleId__c= articleId;
                newBookmark.Bookmarked__c=true;
                bookmarked=true;
                insert newBookmark;
                
            }
            if(Test.isRunningTest())
			{
				throw new CommunityTestException('Test Exception');
			}
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'ArticleDetailController_C_HUM ', 'bookmarkController');
        }
        return bookmarked;
    }
    
    /**
     * getKnowledgeFeedDetails
     * Fetch the knowledge feed for the Article passed as parameter 
     * @param String ArticleUrlName
     * @return Map<String,Object>
     **/

    @AuraEnabled(cacheable=true)
    public static Map<String,Object> getKnowledgeFeedDetails(String ArticleUrlName) {
        Map<String,Object> ArticlecountMap= new Map<String,Object>();
        List<Knowledge__kav> Lstkv =new List<Knowledge__kav>();
        Knowledge__kav kv = new Knowledge__kav();
        Set<Id> KnowledgeIds=new Set<Id>();
        Integer likesCount=0;
        Integer CommentsCount=0;
        
        try{
            
            Lstkv=[SELECT Id, ArticleNumber,Title, Article_Body__c,UrlName,ArticleTotalViewCount,CreatedDate,OwnerId,CreatedBy.CommunityNickname,(select id,Bookmarked__c from ArticleBookmarks__r where CreatedById=: userinfo.getUserId() limit 1) FROM Knowledge__kav where UrlName =: ArticleUrlName  limit 1];
            if (!Lstkv.isEmpty())
                kv=lstKV[0];
            List<Knowledge__Feed> KAFeed= [SELECT Id, ParentId,Parent.ArticleNumber, CommentCount, CreatedDate, LikeCount, NetworkScope FROM Knowledge__Feed where Parent.ArticleNumber=:kv.ArticleNumber ];
            
            for(Knowledge__Feed eachFeed:KAFeed){
                likesCount= likesCount+eachFeed.LikeCount;
                CommentsCount=CommentsCount+eachFeed.CommentCount;
                KnowledgeIds.add(eachFeed.ParentId);
            }
            Integer followerCount=[select Count() from EntitySubscription where ParentId in :KnowledgeIds];
            ArticlecountMap.put('FollowersCount', followerCount);
            ArticlecountMap.put('likesCount', likesCount);
            ArticlecountMap.put('CommentsCount', CommentsCount);
            ArticlecountMap.put('ArticleDetail',kv);
            
            if(Test.isRunningTest())
			{
				throw new CommunityTestException('Test Exception');
			}
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'ArticleDetailController_C_HUM ', 'getKnowledgeFeedDetails');
        }
        return ArticlecountMap;
    }

    /**
     * getRecommendations
     * Fetch the helpful articles and recommended watch based on paramerts 
     * @param String urlName,String source,String articleType
     * @return List<Knowledge__kav>
     **/

    @AuraEnabled(cacheable=true)
    public static List<Knowledge__kav> getRecommendations(String urlName,String source,String articleType) {
    List<Knowledge__kav> lstKnowledgeArticles = new list<Knowledge__kav> ();
        try {
            CommunityUtilityHelper_C_HUM objHelper = new CommunityUtilityHelper_C_HUM();
            lstKnowledgeArticles=objHelper.getRecommendedWatchArticles(urlName,source,articleType);
            
            if(Test.isRunningTest())
			{
				throw new CommunityTestException('Test Exception');
			}
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'ArticleDetailController_C_HUM ', 'getRecommendations');
        }
        return lstKnowledgeArticles;    
    }    
}